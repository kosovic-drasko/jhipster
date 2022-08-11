import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDialog, getDialogIdentifier } from '../dialog.model';

export type EntityResponseType = HttpResponse<IDialog>;
export type EntityArrayResponseType = HttpResponse<IDialog[]>;

@Injectable({ providedIn: 'root' })
export class DialogService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/dialogs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(dialog: IDialog): Observable<EntityResponseType> {
    return this.http.post<IDialog>(this.resourceUrl, dialog, { observe: 'response' });
  }

  update(dialog: IDialog): Observable<EntityResponseType> {
    return this.http.put<IDialog>(`${this.resourceUrl}/${getDialogIdentifier(dialog) as number}`, dialog, { observe: 'response' });
  }

  partialUpdate(dialog: IDialog): Observable<EntityResponseType> {
    return this.http.patch<IDialog>(`${this.resourceUrl}/${getDialogIdentifier(dialog) as number}`, dialog, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDialog>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDialog[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addDialogToCollectionIfMissing(dialogCollection: IDialog[], ...dialogsToCheck: (IDialog | null | undefined)[]): IDialog[] {
    const dialogs: IDialog[] = dialogsToCheck.filter(isPresent);
    if (dialogs.length > 0) {
      const dialogCollectionIdentifiers = dialogCollection.map(dialogItem => getDialogIdentifier(dialogItem)!);
      const dialogsToAdd = dialogs.filter(dialogItem => {
        const dialogIdentifier = getDialogIdentifier(dialogItem);
        if (dialogIdentifier == null || dialogCollectionIdentifiers.includes(dialogIdentifier)) {
          return false;
        }
        dialogCollectionIdentifiers.push(dialogIdentifier);
        return true;
      });
      return [...dialogsToAdd, ...dialogCollection];
    }
    return dialogCollection;
  }
}
