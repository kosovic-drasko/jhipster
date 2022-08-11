import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IDialog } from '../dialog.model';
import { DialogService } from '../service/dialog.service';

@Component({
  templateUrl: './dialog-delete-dialog.component.html',
})
export class DialogDeleteDialogComponent {
  dialog?: IDialog;

  constructor(protected dialogService: DialogService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.dialogService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
