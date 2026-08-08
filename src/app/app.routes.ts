import { Routes } from '@angular/router';
import {Inside1Component}  from './inside1/inside1.component';
import { PiechartComponent } from './piechart/piechart.component';
import { LoginComponent } from './login/login.component';
import { CreateQuizComponent } from './create/create-quiz.component';
import { RegisterComponent } from './register/register.component';
export const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'create/:id/edit', component: CreateQuizComponent},
  {path: 'create', component: CreateQuizComponent},
  {path: 'quiz/:id/statistics', component: PiechartComponent},
  {path: 'quiz/:id', component: Inside1Component},
  {path: 'inside1', redirectTo: 'quiz/1', pathMatch: 'full'},
  {path: 'piechart', component:PiechartComponent}

];
