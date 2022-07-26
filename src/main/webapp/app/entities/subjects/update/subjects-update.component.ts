import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ISubjects, Subjects } from '../subjects.model';
import { SubjectsService } from '../service/subjects.service';

@Component({
  selector: 'jhi-subjects-update',
  templateUrl: './subjects-update.component.html',
})
export class SubjectsUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    subjectName: [null, [Validators.required]],
    numberSmestar: [null, [Validators.required]],
  });

  constructor(protected subjectsService: SubjectsService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ subjects }) => {
      this.updateForm(subjects);
    });
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
      numberSmestar: subjects.numberSmestar,
    });
  }

  protected createFromForm(): ISubjects {
    return {
      ...new Subjects(),
      id: this.editForm.get(['id'])!.value,
      subjectName: this.editForm.get(['subjectName'])!.value,
      numberSmestar: this.editForm.get(['numberSmestar'])!.value,
    };
  }
}
