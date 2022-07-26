import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISubjects, Subjects } from '../subjects.model';
import { SubjectsService } from '../service/subjects.service';

@Injectable({ providedIn: 'root' })
export class SubjectsRoutingResolveService implements Resolve<ISubjects> {
  constructor(protected service: SubjectsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISubjects> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((subjects: HttpResponse<Subjects>) => {
          if (subjects.body) {
            return of(subjects.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Subjects());
  }
}
