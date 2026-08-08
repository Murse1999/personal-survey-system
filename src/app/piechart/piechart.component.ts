import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  QueryList,
  ViewChildren
} from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Chart } from 'chart.js/auto';

import {
  QuestionStat,
  QuizApiService,
  QuizStatResponse
} from '../core/quiz-api.service';

@Component({
  selector: 'app-piechart',
  imports: [RouterLink],
  templateUrl: './piechart.component.html',
  styleUrl: './piechart.component.scss'
})
export class PiechartComponent implements OnInit, OnDestroy {
  @ViewChildren('chartCanvas') private chartCanvases!: QueryList<ElementRef<HTMLCanvasElement>>;

  statistics?: QuizStatResponse;
  isLoading = true;
  errorMessage = '';
  private charts: Chart[] = [];

  constructor(
    private route: ActivatedRoute,
    private quizApi: QuizApiService,
    private changeDetector: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const quizId = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isInteger(quizId)) {
      this.errorMessage = '統計網址不正確';
      this.isLoading = false;
      return;
    }

    this.quizApi.getStatistics(quizId).subscribe({
      next: (statistics) => {
        this.statistics = statistics;
        this.isLoading = false;
        this.changeDetector.detectChanges();
        requestAnimationFrame(() => this.renderCharts());
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = error.status === 403
          ? '你沒有權限查看這份問卷統計'
          : '目前無法取得統計資料';
      }
    });
  }

  ngOnDestroy(): void {
    this.charts.forEach((chart) => chart.destroy());
  }

  hasOptions(question: QuestionStat): boolean {
    return Boolean(question.optionStats?.length);
  }

  private renderCharts(): void {
    this.charts.forEach((chart) => chart.destroy());
    this.charts = [];

    const canvasRefs = this.chartCanvases.toArray();

    for (const question of this.statistics?.questionStats ?? []) {
      if (!question.optionStats?.length) {
        continue;
      }

      const canvas = canvasRefs.find((canvasRef) =>
        canvasRef.nativeElement.dataset['questionId'] === String(question.questionId)
      )?.nativeElement;

      if (!canvas) {
        continue;
      }

      const chart = new Chart(canvas, {
        type: 'pie',
        data: {
          labels: question.optionStats.map((option) => option.optionText),
          datasets: [{
            data: question.optionStats.map((option) => option.selectedCount),
            backgroundColor: [
              '#70b8ff',
              '#ff9b9b',
              '#a9f3c4',
              '#ffd58a',
              '#c7a7ff',
              '#8de0d6'
            ],
            borderColor: '#111522',
            borderWidth: 2
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              position: 'bottom',
              labels: {
                color: '#f7f7f7',
                font: { size: 13, weight: 'bold' }
              }
            }
          }
        }
      });

      this.charts.push(chart);
    }
  }
}
