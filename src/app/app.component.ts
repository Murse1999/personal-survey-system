import { AfterViewInit, Component, ElementRef, NgZone, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NavigationEnd, Router, RouterLink, RouterOutlet } from '@angular/router';
import { Subscription } from 'rxjs';
import * as THREE from 'three';

import {
  AuthService,
  AvatarType,
  CurrentUser,
  UserProfileUpdateRequest
} from './core/auth.service';
import { QuizApiService, QuizResponseDto } from './core/quiz-api.service';

// 先定義一筆問卷資料應該有哪些欄位，這樣 TypeScript 比較知道資料長什麼樣子。
interface QuizItem {
  id: number;
  title: string;
  description: string | null;
  status: string;
  startDate: string;
  endDate: string;
  isPublished: boolean;
}

@Component({
  selector: 'app-root',
  imports: [FormsModule, RouterLink, RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements AfterViewInit, OnDestroy, OnInit {
  // 這個 canvas 是 HTML 裡的背景畫布，Three.js 會把 shader 畫在這裡。
  @ViewChild('shaderCanvas') private shaderCanvas?: ElementRef<HTMLCanvasElement>;
  @ViewChild('introCanvas') private introCanvas?: ElementRef<HTMLCanvasElement>;

  showIntro = true;

  // search 會接住 HTML 裡 [(ngModel)]="search" 輸入的問卷標題關鍵字。
  search = '';

  // firsttime / endtime 會接住 HTML 日期欄位的值，格式會是 2026-06-01。
  firsttime = '';
  endtime = '';

  // allQuizzes 會由後端 API 載入，不再使用寫死的展示資料。
  allQuizzes: QuizItem[] = [];

  // filteredQuizzes 是畫面真正要顯示的資料。預設先顯示全部問卷。
  filteredQuizzes: QuizItem[] = this.allQuizzes;
  pageSize = 10;
  currentPage = 1;
  isLoadingQuizzes = true;
  quizError = '';
  currentUser: CurrentUser | null = null;
  myQuizzes: QuizItem[] = [];
  isMyQuizzesLoading = false;
  myQuizzesError = '';
  isAccountPanelOpen = false;
  isSettingsOpen = false;
  isLoadingProfile = false;
  isSavingProfile = false;
  profileError = '';
  settingsName = '';
  settingsPhone = '';
  settingsAge: number | null = null;
  settingsAvatarType: AvatarType = 'MALE';

  private renderer?: THREE.WebGLRenderer;
  private scene?: THREE.Scene;
  private camera?: THREE.Camera;
  private shaderUniforms?: ShaderUniforms;
  private animationId = 0;
  private introRenderer?: THREE.WebGLRenderer;
  private introScene?: THREE.Scene;
  private introCamera?: THREE.OrthographicCamera;
  private introUniforms?: PaperShaderUniforms;
  private introAnimationId = 0;
  private introTimerId = 0;
  private currentUserSubscription?: Subscription;
  private routerSubscription?: Subscription;

  constructor(
    private ngZone: NgZone,
    private router: Router,
    private quizApi: QuizApiService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.currentUserSubscription = this.authService.currentUser$.subscribe((user) => {
      this.currentUser = user;

      if (user) {
        this.loadMyQuizzes();
      } else {
        this.myQuizzes = [];
        this.isAccountPanelOpen = false;
        this.isSettingsOpen = false;
      }
    });

    this.routerSubscription = this.router.events.subscribe((event) => {
      if (!(event instanceof NavigationEnd) || event.urlAfterRedirects !== '/') {
        return;
      }

      this.loadPublicQuizzes();
      if (this.currentUser) {
        this.loadMyQuizzes();
      }
    });

    this.loadPublicQuizzes();
  }

  isInsidePage(): boolean {
    return this.router.url.startsWith('/inside1');
  }
  piechartPage(): boolean {
  return this.router.url.startsWith('/piechart');
  }
  loginPage(): boolean {
    return this.router.url.startsWith('/login');
  }

  registerPage(): boolean {
    return this.router.url.startsWith('/register');
  }

  forgotPasswordPage(): boolean {
    return this.router.url.startsWith('/forgot-password');
  }

  createPage(): boolean {
    return this.router.url.startsWith('/create');
  }

  quizPage(): boolean {
    return this.router.url.startsWith('/quiz');
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  isAdmin(): boolean {
    return this.authService.getCurrentUser()?.role === 'ADMIN';
  }

  roleLabel(role: string | undefined): string {
    return role === 'ADMIN' ? '管理員' : '一般使用者';
  }

  logout(): void {
    this.authService.logout();
    this.router.navigateByUrl('/');
  }

  toggleAccountPanel(): void {
    this.isAccountPanelOpen = !this.isAccountPanelOpen;
  }

  openSettings(): void {
    if (!this.currentUser) {
      return;
    }

    this.isSettingsOpen = true;
    this.profileError = '';
    this.syncSettings(this.currentUser);
    this.isLoadingProfile = true;

    this.authService.getMyProfile().subscribe({
      next: (profile) => {
        this.syncSettings(profile);
        this.isLoadingProfile = false;
      },
      error: () => {
        this.isLoadingProfile = false;
        this.profileError = '目前無法載入使用者設定';
      }
    });
  }

  closeSettings(): void {
    this.isSettingsOpen = false;
    this.profileError = '';
  }

  selectAvatar(type: AvatarType): void {
    this.settingsAvatarType = type;
  }

  saveProfile(): void {
    this.profileError = '';

    if (!this.settingsName.trim() || !this.settingsPhone.trim()) {
      this.profileError = '請完成姓名和電話';
      return;
    }

    if (
      this.settingsAge !== null
      && (!Number.isInteger(this.settingsAge)
        || this.settingsAge < 0
        || this.settingsAge > 120)
    ) {
      this.profileError = '年齡請填寫 0 到 120 的整數';
      return;
    }

    const request: UserProfileUpdateRequest = {
      name: this.settingsName.trim(),
      phone: this.settingsPhone.trim(),
      age: this.settingsAge,
      avatarType: this.settingsAvatarType
    };

    this.isSavingProfile = true;
    this.authService.updateMyProfile(request).subscribe({
      next: () => {
        this.isSavingProfile = false;
        this.isSettingsOpen = false;
      },
      error: (error) => {
        this.isSavingProfile = false;
        this.profileError = error.error || '更新使用者設定失敗';
      }
    });
  }

  avatarImage(type: AvatarType | undefined): string {
    return type === 'FEMALE'
      ? 'avatars/female-avatar.svg'
      : 'avatars/male-avatar.svg';
  }

  canManageQuiz(quizId: number): boolean {
    return this.isAdmin() || this.myQuizzes.some((quiz) => quiz.id === quizId);
  }

  get totalPages(): number {
    return Math.max(1, Math.ceil(this.filteredQuizzes.length / this.pageSize));
  }

  get pagedQuizzes(): QuizItem[] {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    return this.filteredQuizzes.slice(startIndex, startIndex + this.pageSize);
  }

  onPageSizeChange(): void {
    this.currentPage = 1;
  }

  previousPage(): void {
    this.currentPage = Math.max(1, this.currentPage - 1);
  }

  nextPage(): void {
    this.currentPage = Math.min(this.totalPages, this.currentPage + 1);
  }

  deleteMyQuiz(quiz: QuizItem): void {
    if (!window.confirm(`確定要刪除「${quiz.title}」嗎？刪除後無法復原。`)) {
      return;
    }

    this.quizApi.deleteQuiz(quiz.id).subscribe({
      next: () => {
        this.loadPublicQuizzes();
        this.loadMyQuizzes();
      },
      error: (error) => {
        this.myQuizzesError = error.status === 403
          ? '你沒有權限刪除這份問卷'
          : '刪除問卷失敗，請稍後再試';
      }
    });
  }

  private loadPublicQuizzes(): void {
    this.isLoadingQuizzes = true;
    this.quizError = '';

    this.quizApi.getQuizzes().subscribe({
      next: (quizzes) => {
        this.allQuizzes = quizzes.map((quiz) => this.toQuizItem(quiz));
        this.filteredQuizzes = this.allQuizzes;
        this.currentPage = 1;
        this.isLoadingQuizzes = false;
      },
      error: () => {
        this.quizError = '目前無法取得問卷，請確認後端是否啟動。';
        this.isLoadingQuizzes = false;
      }
    });
  }

  private loadMyQuizzes(): void {
    this.isMyQuizzesLoading = true;
    this.myQuizzesError = '';

    this.quizApi.getMyQuizzes().subscribe({
      next: (quizzes) => {
        this.myQuizzes = quizzes.map((quiz) => this.toQuizItem(quiz));
        this.isMyQuizzesLoading = false;
      },
      error: () => {
        this.myQuizzesError = '目前無法取得你的問卷';
        this.isMyQuizzesLoading = false;
      }
    });
  }

  private syncSettings(user: Pick<CurrentUser, 'name' | 'phone' | 'age' | 'avatarType'>): void {
    this.settingsName = user.name;
    this.settingsPhone = user.phone;
    this.settingsAge = user.age;
    this.settingsAvatarType = user.avatarType;
  }

  searchQuiz(): void {
    // 標題、開始日期、結束日期都是可選條件，可以只填其中一項。
    const searchKeyword = this.search.trim().toLocaleLowerCase();

    this.filteredQuizzes = this.allQuizzes.filter((quiz) => {
      const quizStartDate = this.toInputDate(quiz.startDate);
      const quizEndDate = this.toInputDate(quiz.endDate);

      const titleMatches = searchKeyword === ''
        || quiz.title.toLocaleLowerCase().includes(searchKeyword);
      const startDateMatches = this.firsttime === ''
        || quizStartDate >= this.firsttime;
      const endDateMatches = this.endtime === ''
        || quizEndDate <= this.endtime;

      return titleMatches && startDateMatches && endDateMatches;
    });
    this.currentPage = 1;
  }

  private toQuizItem(quiz: QuizResponseDto): QuizItem {
    return {
      id: quiz.id,
      title: quiz.title,
      description: quiz.description,
      status: this.getQuizStatus(quiz),
      startDate: this.formatDate(quiz.startDate),
      endDate: this.formatDate(quiz.endDate),
      isPublished: quiz.isPublished
    };
  }

  private getQuizStatus(quiz: QuizResponseDto): string {
    if (!quiz.isPublished) {
      return '未發布';
    }

    const now = new Date();
    const startDate = new Date(quiz.startDate);
    const endDate = new Date(quiz.endDate);

    if (now < startDate) {
      return '尚未開始';
    }

    if (now > endDate) {
      return '已結束';
    }

    return '進行中';
  }

  private formatDate(dateText: string): string {
    return dateText.slice(0, 10).replaceAll('-', '/');
  }

  ngAfterViewInit(): void {
    // 背景動畫每一幀都會更新，不需要讓 Angular 重新檢查畫面，所以放在 runOutsideAngular 比較順。
    this.ngZone.runOutsideAngular(() => {
      this.createShaderBackground();
      this.createIntroBackground();
      this.animateShaderBackground();
      this.animateIntroBackground();
      window.addEventListener('resize', this.resizeShaderBackground);
      window.addEventListener('resize', this.resizeIntroBackground);

      this.introTimerId = window.setTimeout(() => {
        this.ngZone.run(() => {
          this.showIntro = false;
          if (this.router.url === '/') {
            this.router.navigateByUrl('/');
          }
          this.stopIntroBackground();
        });
      }, 2000);
    });
  }

  ngOnDestroy(): void {
    // 離開頁面時停止動畫與釋放資源，避免背景還在偷偷運作。
    cancelAnimationFrame(this.animationId);
    window.clearTimeout(this.introTimerId);
    this.stopIntroBackground();
    this.routerSubscription?.unsubscribe();
    window.removeEventListener('resize', this.resizeShaderBackground);
    window.removeEventListener('resize', this.resizeIntroBackground);
    this.renderer?.dispose();
    this.currentUserSubscription?.unsubscribe();
  }

  private toInputDate(dateText: string): string {
    // 把 2026/06/01 轉成 2026-06-01，方便跟 input type="date" 的值比較。
    return dateText.replaceAll('/', '-');
  }

  private createShaderBackground(): void {
    if (!this.shaderCanvas) {
      return;
    }

    const width = window.innerWidth;
    const height = window.innerHeight;
    const canvas = this.shaderCanvas.nativeElement;

    this.scene = new THREE.Scene();
    this.camera = new THREE.Camera();
    this.camera.position.z = 1;

    this.renderer = new THREE.WebGLRenderer({ canvas, alpha: false, antialias: true });
    this.renderer.setPixelRatio(window.devicePixelRatio);
    this.renderer.setSize(width, height, false);

    this.shaderUniforms = {
      time: { value: 0 },
      resolution: { value: new THREE.Vector2(this.renderer.domElement.width, this.renderer.domElement.height) }
    };

    const shaderPlane = new THREE.Mesh(
      new THREE.PlaneGeometry(2, 2),
      new THREE.ShaderMaterial({
        uniforms: this.shaderUniforms,
        vertexShader,
        fragmentShader,
        transparent: false,
        side: THREE.DoubleSide
      })
    );
    this.scene.add(shaderPlane);
  }

  private createIntroBackground(): void {
    if (!this.introCanvas) {
      return;
    }

    const width = window.innerWidth;
    const height = window.innerHeight;
    const canvas = this.introCanvas.nativeElement;

    this.introScene = new THREE.Scene();
    this.introCamera = new THREE.OrthographicCamera(-1, 1, 1, -1, 0, 1);

    this.introRenderer = new THREE.WebGLRenderer({ canvas, alpha: true, antialias: true });
    this.introRenderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
    this.introRenderer.setSize(width, height, false);

    this.introUniforms = {
      time: { value: 0 },
      intensity: { value: 1 },
      color1: { value: new THREE.Color('#ff5722') },
      color2: { value: new THREE.Color('#ffffff') }
    };

    const introPlane = new THREE.Mesh(
      new THREE.PlaneGeometry(2, 2, 32, 32),
      new THREE.ShaderMaterial({
        uniforms: this.introUniforms,
        vertexShader: paperVertexShader,
        fragmentShader: paperFragmentShader,
        transparent: true,
        side: THREE.DoubleSide
      })
    );

    this.introScene.add(introPlane);
  }

  private animateShaderBackground = (): void => {
    if (!this.renderer || !this.scene || !this.camera || !this.shaderUniforms) {
      return;
    }

    this.shaderUniforms.time.value += 0.05;
    this.renderer.render(this.scene, this.camera);
    this.animationId = requestAnimationFrame(this.animateShaderBackground);
  };

  private animateIntroBackground = (): void => {
    if (!this.introRenderer || !this.introScene || !this.introCamera || !this.introUniforms) {
      return;
    }

    const time = performance.now() * 0.001;
    this.introUniforms.time.value = time;
    this.introUniforms.intensity.value = 1 + Math.sin(time * 2) * 0.3;

    this.introRenderer.render(this.introScene, this.introCamera);
    this.introAnimationId = requestAnimationFrame(this.animateIntroBackground);
  };

  private resizeShaderBackground = (): void => {
    if (!this.renderer || !this.camera || !this.shaderUniforms) {
      return;
    }

    const width = window.innerWidth;
    const height = window.innerHeight;
    this.renderer.setSize(width, height, false);
    this.shaderUniforms.resolution.value.set(
      this.renderer.domElement.width,
      this.renderer.domElement.height
    );
  };

  private resizeIntroBackground = (): void => {
    if (!this.introRenderer) {
      return;
    }

    this.introRenderer.setSize(window.innerWidth, window.innerHeight, false);
  };

  private stopIntroBackground(): void {
    cancelAnimationFrame(this.introAnimationId);
    this.introRenderer?.dispose();
    this.introRenderer = undefined;
    this.introScene = undefined;
    this.introCamera = undefined;
    this.introUniforms = undefined;
  }
}

interface ShaderUniforms extends Record<string, THREE.IUniform> {
  time: THREE.IUniform<number>;
  resolution: THREE.IUniform<THREE.Vector2>;
}

interface PaperShaderUniforms extends Record<string, THREE.IUniform> {
  time: THREE.IUniform<number>;
  intensity: THREE.IUniform<number>;
  color1: THREE.IUniform<THREE.Color>;
  color2: THREE.IUniform<THREE.Color>;
}

const vertexShader = `
  void main() {
    gl_Position = vec4(position, 1.0);
  }
`;

const paperVertexShader = `
  uniform float time;
  uniform float intensity;
  varying vec2 vUv;
  varying vec3 vPosition;

  void main() {
    vUv = uv;
    vPosition = position;

    vec3 pos = position;
    pos.y += sin(pos.x * 10.0 + time) * 0.1 * intensity;
    pos.x += cos(pos.y * 8.0 + time * 1.5) * 0.05 * intensity;

    gl_Position = projectionMatrix * modelViewMatrix * vec4(pos, 1.0);
  }
`;

const paperFragmentShader = `
  uniform float time;
  uniform float intensity;
  uniform vec3 color1;
  uniform vec3 color2;
  varying vec2 vUv;
  varying vec3 vPosition;

  void main() {
    vec2 uv = vUv;

    float noise = sin(uv.x * 20.0 + time) * cos(uv.y * 15.0 + time * 0.8);
    noise += sin(uv.x * 35.0 - time * 2.0) * cos(uv.y * 25.0 + time * 1.2) * 0.5;

    vec3 color = mix(color1, color2, noise * 0.5 + 0.5);
    color = mix(color, vec3(1.0), pow(abs(noise), 2.0) * intensity);

    float glow = 1.0 - length(uv - 0.5) * 2.0;
    glow = pow(glow, 2.0);

    gl_FragColor = vec4(color * glow, glow * 0.8);
  }
`;

const fragmentShader = `
  #define TWO_PI 6.2831853072
  #define PI 3.14159265359

  precision highp float;
  uniform float time;
  uniform vec2 resolution;
  varying vec2 vUv;

  float random(in float x) {
    return fract(sin(x) * 1e4);
  }

  float random(vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
  }

  void main() {
    vec2 uv = (gl_FragCoord.xy * 2.0 - resolution.xy) / min(resolution.x, resolution.y);

    vec2 fMosaicScal = vec2(4.0, 2.0);
    vec2 vScreenSize = vec2(256, 256);
    uv.x = floor(uv.x * vScreenSize.x / fMosaicScal.x) / (vScreenSize.x / fMosaicScal.x);
    uv.y = floor(uv.y * vScreenSize.y / fMosaicScal.y) / (vScreenSize.y / fMosaicScal.y);

    float t = time * 0.06 + random(uv.x) * 0.4;
    float lineWidth = 0.0008;

    vec3 color = vec3(0.0);
    for(int j = 0; j < 3; j++) {
      for(int i = 0; i < 5; i++) {
        color[j] += lineWidth * float(i * i) / abs(fract(t - 0.01 * float(j) + float(i) * 0.01) * 1.0 - length(uv));
      }
    }

    gl_FragColor = vec4(color[2], color[1], color[0], 1.0);
  }
`;
