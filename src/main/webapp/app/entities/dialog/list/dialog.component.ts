import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IDialog } from '../dialog.model';
import { DialogService } from '../service/dialog.service';
import { DialogDeleteDialogComponent } from '../delete/dialog-delete-dialog.component';
import { DialogUpdateComponent } from '../update/dialog-update.component';

@Component({
  selector: 'jhi-dialog',
  templateUrl: './dialog.component.html',
})
export class DialogComponent implements OnInit {
  dialogs?: IDialog[];
  isLoading = false;

  constructor(protected dialogService: DialogService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.dialogService.query().subscribe({
      next: (res: HttpResponse<IDialog[]>) => {
        this.isLoading = false;
        this.dialogs = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IDialog): number {
    return item.id!;
  }

  delete(dialog: IDialog): void {
    const modalRef = this.modalService.open(DialogDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.dialog = this.dialogs;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      this.loadAll();
    });
  }

  update(id: any, name: any): void {
    const modalRef = this.modalService.open(DialogUpdateComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.id = id;
    modalRef.componentInstance.name = name;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }

  add(): void {
    const modalRef = this.modalService.open(DialogUpdateComponent, { size: 'lg', backdrop: 'static' });
    // modalRef.componentInstance.id = id;

    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
