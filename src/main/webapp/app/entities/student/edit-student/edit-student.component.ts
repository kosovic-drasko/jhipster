import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { StudentService } from '../service/student.service';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { IStudent, Student } from '../student.model';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'jhi-edit-student',
  templateUrl: './edit-student.component.html',
  styleUrls: ['./edit-student.component.scss'],
})
export class EditStudentComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    age: [null, [Validators.required]],
  });

  constructor(
    protected activeModal: NgbActiveModal,
    protected studentService: StudentService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ student }) => {
      this.updateForm(student);
    });
  }
  cancel(): void {
    this.activeModal.dismiss();
  }
  previousState(): void {
    this.activeModal.dismiss();
  }
  save(): void {
    const student = this.createFromForm();
    this.subscribeToSaveResponse(this.studentService.create(student));
    this.activeModal.close('dodato');
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

  protected onSaveSuccess(): void {
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
