import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ISubjects } from '../subjects.model';

import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/config/pagination.constants';
import { SubjectsService } from '../service/subjects.service';
import { SubjectsDeleteDialogComponent } from '../delete/subjects-delete-dialog.component';

@Component({
  selector: 'jhi-subjects',
  templateUrl: './subjects.component.html',
})
export class SubjectsComponent implements OnInit {
  subjects?: ISubjects[];
  isLoading = false;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  ukupno?: number;
  imePredmeta?: string;

  constructor(
    protected subjectsService: SubjectsService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal
  ) {}

  // getSum(): any {
  //
  //
  //   return (this.ukupno = this.subjects?.map(t => t.numberSemestars).reduce((acc, value) => acc! + value!, 0));
  //   console.log('Uvecane godine ===========>  ', this.ukupno);
  // }

  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    this.subjectsService
      .query({
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: HttpResponse<ISubjects[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
          // this.subjects = res.body?.filter(val => val.subjectName === this.imePredmeta);
          this.ukupno = res.body?.reduce((acc, productsdet) => acc + productsdet.numberSemestars!, 0);
        },
        error: () => {
          this.isLoading = false;
          this.onError();
        },
      });
  }

  loadPageByNameSubject(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    this.subjectsService
      .query({
        'subjectName.contains': this.imePredmeta,
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: HttpResponse<ISubjects[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
          this.ukupno = res.body?.reduce((acc, productsdet) => acc + productsdet.numberSemestars!, 0);
        },
        error: () => {
          this.isLoading = false;
          this.onError();
        },
      });
  }
  imePredmetaNull(): void {
    this.imePredmeta = '';
    this.loadPage();
  }
  ngOnInit(): void {
    this.handleNavigation();
  }

  trackId(_index: number, item: ISubjects): number {
    return item.id!;
  }

  delete(subjects: ISubjects): void {
    const modalRef = this.modalService.open(SubjectsDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.subjects = subjects;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadPage();
      }
    });
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? ASC : DESC)];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected handleNavigation(): void {
    combineLatest([this.activatedRoute.data, this.activatedRoute.queryParamMap]).subscribe(([data, params]) => {
      const page = params.get('page');
      const pageNumber = +(page ?? 1);
      const sort = (params.get(SORT) ?? data['defaultSort']).split(',');
      const predicate = sort[0];
      const ascending = sort[1] === ASC;
      if (pageNumber !== this.page || predicate !== this.predicate || ascending !== this.ascending) {
        this.predicate = predicate;
        this.ascending = ascending;
        this.loadPage(pageNumber, true);
      }
    });
  }

  protected onSuccess(data: ISubjects[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/subjects'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? ASC : DESC),
        },
      });
    }
    this.subjects = data ?? [];
    this.ngbPaginationPage = this.page;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }
}
