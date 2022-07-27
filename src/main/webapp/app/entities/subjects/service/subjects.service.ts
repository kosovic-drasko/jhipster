import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISubjects, getSubjectsIdentifier } from '../subjects.model';

export type EntityResponseType = HttpResponse<ISubjects>;
export type EntityArrayResponseType = HttpResponse<ISubjects[]>;

@Injectable({ providedIn: 'root' })
export class SubjectsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/subjects');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(subjects: ISubjects): Observable<EntityResponseType> {
    return this.http.post<ISubjects>(this.resourceUrl, subjects, { observe: 'response' });
  }

  update(subjects: ISubjects): Observable<EntityResponseType> {
    return this.http.put<ISubjects>(`${this.resourceUrl}/${getSubjectsIdentifier(subjects) as number}`, subjects, { observe: 'response' });
  }

  partialUpdate(subjects: ISubjects): Observable<EntityResponseType> {
    return this.http.patch<ISubjects>(`${this.resourceUrl}/${getSubjectsIdentifier(subjects) as number}`, subjects, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISubjects>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISubjects[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  query1(): Observable<ISubjects[]> {
    return this.http.get<ISubjects[]>(this.resourceUrl);
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addSubjectsToCollectionIfMissing(subjectsCollection: ISubjects[], ...subjectsToCheck: (ISubjects | null | undefined)[]): ISubjects[] {
    const subjects: ISubjects[] = subjectsToCheck.filter(isPresent);
    if (subjects.length > 0) {
      const subjectsCollectionIdentifiers = subjectsCollection.map(subjectsItem => getSubjectsIdentifier(subjectsItem)!);
      const subjectsToAdd = subjects.filter(subjectsItem => {
        const subjectsIdentifier = getSubjectsIdentifier(subjectsItem);
        if (subjectsIdentifier == null || subjectsCollectionIdentifiers.includes(subjectsIdentifier)) {
          return false;
        }
        subjectsCollectionIdentifiers.push(subjectsIdentifier);
        return true;
      });
      return [...subjectsToAdd, ...subjectsCollection];
    }
    return subjectsCollection;
  }
}
