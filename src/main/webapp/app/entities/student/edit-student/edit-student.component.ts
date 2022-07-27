import { Component, Input } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { StudentService } from '../service/student.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { IStudent, Student } from '../student.model';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'jhi-edit-student',
  templateUrl: './edit-student.component.html',
  styleUrls: ['./edit-student.component.scss'],
})
export class EditStudentComponent {
  isSaving = false;
  editForm: FormGroup;
  students?: IStudent | any[];

  @Input() public id?: any;
  @Input() public name?: any;
  @Input() public age?: any;

  constructor(
    protected activeModal: NgbActiveModal,
    protected studentService: StudentService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder,
    protected router: Router
  ) {
    this.editForm = this.fb.group({
      id: [this.id, [Validators.required]],
      name: [this.name, [Validators.required]],
      age: [this.age],
    });
  }

  // loadAll():any {
  //   if (this.user) {
  //     return this.studentService.find(this.user).pipe(
  //       mergeMap((student: HttpResponse<Student>) => {
  //         if (student.body) {
  //           return of(student.body);
  //           console.log('to je ', student.body);
  //         } else {
  //           this.router.navigate(['404']);
  //           return EMPTY;
  //         }
  //       })
  //     );
  //
  // }
  // }
  // loadAll2(): void {
  //   this.studentService.query(
  //     {
  //       'id.in': this.user,
  //     }
  //   ).subscribe({
  //     next: (res: HttpResponse<IStudent[]>) => {
  //       this.students = res.body ?? [];
  //       console.log('to je ', this.user);
  //       console.log('Iz loadAll2',this.students);
  //     }
  //   });

  cancel(): void {
    this.activeModal.dismiss();
  }

  previousState(): void {
    this.activeModal.dismiss();
  }

  save(): void {
    const student = this.createFromForm();
    this.subscribeToSaveResponse(this.studentService.update(student));
    this.activeModal.close('izmjenjeno');
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
