import { Component, Inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable, pipe } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ISubjects, Subjects } from '../subjects.model';
import { SubjectsService } from '../service/subjects.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'jhi-subjects-update',
  templateUrl: './subjects-update.component.html',
})
export class SubjectsUpdateComponent implements OnInit {
  isSaving = false;
  editForm: FormGroup;

  constructor(
    protected subjectsService: SubjectsService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder,
    private dialogRef: MatDialogRef<SubjectsUpdateComponent>,
    @Inject(MAT_DIALOG_DATA)
    { id, subjectName, numberSemestars }: any
  ) {
    this.editForm = this.fb.group({
      id: [id],
      subjectName: [subjectName, [Validators.required]],
      numberSemestars: [numberSemestars, [Validators.required]],
    });
  }
  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ subjects }) => {
      this.updateForm(subjects);
    });
  }
  saveSubject(): void {
    this.isSaving = true;
    const postupci = this.createFromForm();
    this.subscribeToSaveResponse(this.subjectsService.update(postupci));
  }
  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const subjects = this.createFromForm();
    if (subjects.id !== undefined) {
      this.subscribeToSaveResponse(this.subjectsService.update(subjects));
    } else {
      this.subscribeToSaveResponse(this.subjectsService.create(subjects));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISubjects>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(subjects: ISubjects): void {
    this.editForm.patchValue({
      id: subjects.id,
      subjectName: subjects.subjectName,
      numberSemestars: subjects.numberSemestars,
    });
  }

  protected createFromForm(): ISubjects {
    return {
      ...new Subjects(),
      id: this.editForm.get(['id'])!.value,
      subjectName: this.editForm.get(['subjectName'])!.value,
      numberSemestars: this.editForm.get(['numberSemestars'])!.value,
    };
  }
}
