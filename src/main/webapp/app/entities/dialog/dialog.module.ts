import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { DialogComponent } from './list/dialog.component';
import { DialogDetailComponent } from './detail/dialog-detail.component';
import { DialogUpdateComponent } from './update/dialog-update.component';
import { DialogDeleteDialogComponent } from './delete/dialog-delete-dialog.component';
import { DialogRoutingModule } from './route/dialog-routing.module';

@NgModule({
  imports: [SharedModule, DialogRoutingModule],
  declarations: [DialogComponent, DialogDetailComponent, DialogUpdateComponent, DialogDeleteDialogComponent],
  entryComponents: [DialogDeleteDialogComponent],
})
export class DialogModule {}
