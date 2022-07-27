import { Component } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { IStudent, Student } from '../student.model';
import { StudentService } from '../service/student.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'jhi-student-update',
  templateUrl: './student-update.component.html',
})
export class StudentUpdateComponent {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    age: [null, [Validators.required]],
  });

  constructor(protected activeModal: NgbActiveModal, protected studentService: StudentService, protected fb: FormBuilder) {}

  previousState(): void {
    this.activeModal.dismiss();
  }

  save(): void {
    const student = this.createFromForm();
    this.subscribeToSaveResponse(this.studentService.create(student));
    this.activeModal.close('promjenjeno');
  }

  // save(): void {
  //   this.isSaving = true;
  //   const student = this.createFromForm();
  //   if (student.id !== undefined) {
  //     this.subscribeToSaveResponse(this.studentService.update(student));
  //   } else {
  //     this.subscribeToSaveResponse(this.studentService.create(student));
  //   }
  // }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IStudent>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(student: IStudent): void {
    this.editForm.patchValue({
      id: student.id,
      name: student.name,
      age: student.age,
    });
  }

  protected createFromForm(): IStudent {
    return {
      ...new Student(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      age: this.editForm.get(['age'])!.value,
    };
  }
}
