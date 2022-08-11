import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDialog, Dialog } from '../dialog.model';
import { DialogService } from '../service/dialog.service';

@Injectable({ providedIn: 'root' })
export class DialogRoutingResolveService implements Resolve<IDialog> {
  constructor(protected service: DialogService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IDialog> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((dialog: HttpResponse<Dialog>) => {
          if (dialog.body) {
            return of(dialog.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Dialog());
  }
}
