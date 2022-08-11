import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'student',
        data: { pageTitle: 'jhipsterApp.student.home.title' },
        loadChildren: () => import('./student/student.module').then(m => m.StudentModule),
      },
      {
        path: 'subjects',
        data: { pageTitle: 'jhipsterApp.subjects.home.title' },
        loadChildren: () => import('./subjects/subjects.module').then(m => m.SubjectsModule),
      },
      {
        path: 'dialog',
        data: { pageTitle: 'jhipsterApp.dialog.home.title' },
        loadChildren: () => import('./dialog/dialog.module').then(m => m.DialogModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
