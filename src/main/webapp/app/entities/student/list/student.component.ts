import { Component, OnInit, ViewChild } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IStudent } from '../student.model';
import { StudentService } from '../service/student.service';
import { StudentDeleteDialogComponent } from '../delete/student-delete-dialog.component';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { StudentUpdateComponent } from '../update/student-update.component';
import { EditStudentComponent } from '../edit-student/edit-student.component';

@Component({
  selector: 'jhi-student',
  templateUrl: './student.component.html',
  styleUrls: ['./student.scss'],
})
export class StudentComponent implements OnInit {
  students?: IStudent[];
  isLoading = false;
  student_age?: number[];
  student_name?: any[];
  ime?: string;
  ukupno?: any;
  public displayedColumns = ['id', 'name', 'age', 'edit', 'delete'];
  public dataSource = new MatTableDataSource<IStudent>();
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  constructor(protected studentService: StudentService, protected modalService: NgbModal) {}
  loadAllRxj(): void {
    this.studentService.query1().subscribe({
      next: res => {
        this.students = res;
        this.student_age = res.map(val => val.age! * 10);
        this.students = res.filter(val => val.name === this.ime);
        this.dataSource.data = res.filter(val => val.name === this.ime);
        this.getTotalProcjenjena();
        // this.ukupno = res.reduce((acc, productsdet) => acc + productsdet.age!, 0);
        console.log('Studenti iz boota ukupno godina======>>  ', this.ukupno);
        console.log('Studenti iz boota name je  ', this.students);
        console.log('Uvecane godine ===========>  ', this.student_age);
      },
    });
  }
  getTotalProcjenjena(): any {
    return (this.ukupno = this.dataSource.filteredData.map(t => t.age).reduce((acc, value) => acc! + value!, 0));
    console.log('Uvecane godine ===========>  ', this.ukupno);
  }
  loadAll(): void {
    this.isLoading = true;
    this.studentService.query().subscribe({
      next: (res: HttpResponse<IStudent[]>) => {
        this.isLoading = false;
        this.students = res.body ?? [];
        this.dataSource.data = res.body ?? [];
        this.getTotalProcjenjena();
        console.log(' ===========>  ', this.students);
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IStudent): number {
    return item.id!;
  }

  delete(student: IStudent): void {
    const modalRef = this.modalService.open(StudentDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.student = student;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }

  add(): void {
    const modalRef = this.modalService.open(StudentUpdateComponent, { size: 'lg', backdrop: 'static' });
    // modalRef.componentInstance.person = person;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'dodato') {
        this.loadAll();
      }
    });
  }

  edit(id: any, name: string, age: any): void {
    const modalRef = this.modalService.open(EditStudentComponent, { size: 'lg', backdrop: 'static' });
    // modalRef.componentInstance.person = person;
    // unsubscribe not needed because closed completes on modal close
    modalRef.componentInstance.id = id;
    modalRef.componentInstance.name = name;
    modalRef.componentInstance.age = age;
    modalRef.closed.subscribe(reason => {
      if (reason === 'izmjenjeno') {
        this.loadAll();
      }
    });
  }
}
