import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DialogComponent } from '../list/dialog.component';
import { DialogDetailComponent } from '../detail/dialog-detail.component';
import { DialogUpdateComponent } from '../update/dialog-update.component';
import { DialogRoutingResolveService } from './dialog-routing-resolve.service';

const dialogRoute: Routes = [
  {
    path: '',
    component: DialogComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DialogDetailComponent,
    resolve: {
      dialog: DialogRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DialogUpdateComponent,
    resolve: {
      dialog: DialogRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DialogUpdateComponent,
    resolve: {
      dialog: DialogRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(dialogRoute)],
  exports: [RouterModule],
})
export class DialogRoutingModule {}
