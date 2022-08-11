import { Component, Input, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IDialog, Dialog } from '../dialog.model';
import { DialogService } from '../service/dialog.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'jhi-dialog-update',
  templateUrl: './dialog-update.component.html',
})
export class DialogUpdateComponent implements OnInit {
  isSaving = false;

  @Input() public dialog: any;
  editForm = this.fb.group({
    id: [],
    name: [],
  });

  constructor(
    protected dialogService: DialogService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder,
    protected activeModal: NgbActiveModal
  ) {}

  ngOnInit(): void {
    // this.activatedRoute.data.subscribe(({ dialog }) => {
    //   this.updateForm(dialog);

    console.log(this.dialog);
    // };
  }

  previousState(): void {
    this.activeModal.close();
  }

  save(): void {
    this.isSaving = true;
    const dialog = this.createFromForm();
    if (dialog.id !== null) {
      this.subscribeToSaveResponse(this.dialogService.update(dialog));
    } else {
      this.subscribeToSaveResponse(this.dialogService.create(dialog));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDialog>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.activeModal.close();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(dialog: IDialog): void {
    this.editForm.patchValue({
      id: dialog.id,
      name: dialog.name,
    });
  }

  protected createFromForm(): IDialog {
    return {
      ...new Dialog(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
    };
  }
}
