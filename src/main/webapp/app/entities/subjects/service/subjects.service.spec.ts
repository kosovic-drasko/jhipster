import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISubjects, Subjects } from '../subjects.model';

import { SubjectsService } from './subjects.service';

describe('Subjects Service', () => {
  let service: SubjectsService;
  let httpMock: HttpTestingController;
  let elemDefault: ISubjects;
  let expectedResult: ISubjects | ISubjects[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SubjectsService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      subjectName: 'AAAAAAA',
      numberSemestars: 0,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Subjects', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Subjects()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Subjects', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          subjectName: 'BBBBBB',
          numberSemestars: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Subjects', () => {
      const patchObject = Object.assign(
        {
          numberSemestars: 1,
        },
        new Subjects()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Subjects', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          subjectName: 'BBBBBB',
          numberSemestars: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Subjects', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addSubjectsToCollectionIfMissing', () => {
      it('should add a Subjects to an empty array', () => {
        const subjects: ISubjects = { id: 123 };
        expectedResult = service.addSubjectsToCollectionIfMissing([], subjects);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(subjects);
      });

      it('should not add a Subjects to an array that contains it', () => {
        const subjects: ISubjects = { id: 123 };
        const subjectsCollection: ISubjects[] = [
          {
            ...subjects,
          },
          { id: 456 },
        ];
        expectedResult = service.addSubjectsToCollectionIfMissing(subjectsCollection, subjects);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Subjects to an array that doesn't contain it", () => {
        const subjects: ISubjects = { id: 123 };
        const subjectsCollection: ISubjects[] = [{ id: 456 }];
        expectedResult = service.addSubjectsToCollectionIfMissing(subjectsCollection, subjects);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(subjects);
      });

      it('should add only unique Subjects to an array', () => {
        const subjectsArray: ISubjects[] = [{ id: 123 }, { id: 456 }, { id: 37092 }];
        const subjectsCollection: ISubjects[] = [{ id: 123 }];
        expectedResult = service.addSubjectsToCollectionIfMissing(subjectsCollection, ...subjectsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const subjects: ISubjects = { id: 123 };
        const subjects2: ISubjects = { id: 456 };
        expectedResult = service.addSubjectsToCollectionIfMissing([], subjects, subjects2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(subjects);
        expect(expectedResult).toContain(subjects2);
      });

      it('should accept null and undefined values', () => {
        const subjects: ISubjects = { id: 123 };
        expectedResult = service.addSubjectsToCollectionIfMissing([], null, subjects, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(subjects);
      });

      it('should return initial array if no Subjects is added', () => {
        const subjectsCollection: ISubjects[] = [{ id: 123 }];
        expectedResult = service.addSubjectsToCollectionIfMissing(subjectsCollection, undefined, null);
        expect(expectedResult).toEqual(subjectsCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
