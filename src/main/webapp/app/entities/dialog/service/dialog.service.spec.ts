import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IDialog, Dialog } from '../dialog.model';

import { DialogService } from './dialog.service';

describe('Dialog Service', () => {
  let service: DialogService;
  let httpMock: HttpTestingController;
  let elemDefault: IDialog;
  let expectedResult: IDialog | IDialog[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(DialogService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
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

    it('should create a Dialog', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Dialog()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Dialog', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Dialog', () => {
      const patchObject = Object.assign({}, new Dialog());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Dialog', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
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

    it('should delete a Dialog', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addDialogToCollectionIfMissing', () => {
      it('should add a Dialog to an empty array', () => {
        const dialog: IDialog = { id: 123 };
        expectedResult = service.addDialogToCollectionIfMissing([], dialog);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(dialog);
      });

      it('should not add a Dialog to an array that contains it', () => {
        const dialog: IDialog = { id: 123 };
        const dialogCollection: IDialog[] = [
          {
            ...dialog,
          },
          { id: 456 },
        ];
        expectedResult = service.addDialogToCollectionIfMissing(dialogCollection, dialog);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Dialog to an array that doesn't contain it", () => {
        const dialog: IDialog = { id: 123 };
        const dialogCollection: IDialog[] = [{ id: 456 }];
        expectedResult = service.addDialogToCollectionIfMissing(dialogCollection, dialog);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dialog);
      });

      it('should add only unique Dialog to an array', () => {
        const dialogArray: IDialog[] = [{ id: 123 }, { id: 456 }, { id: 23312 }];
        const dialogCollection: IDialog[] = [{ id: 123 }];
        expectedResult = service.addDialogToCollectionIfMissing(dialogCollection, ...dialogArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const dialog: IDialog = { id: 123 };
        const dialog2: IDialog = { id: 456 };
        expectedResult = service.addDialogToCollectionIfMissing([], dialog, dialog2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dialog);
        expect(expectedResult).toContain(dialog2);
      });

      it('should accept null and undefined values', () => {
        const dialog: IDialog = { id: 123 };
        expectedResult = service.addDialogToCollectionIfMissing([], null, dialog, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(dialog);
      });

      it('should return initial array if no Dialog is added', () => {
        const dialogCollection: IDialog[] = [{ id: 123 }];
        expectedResult = service.addDialogToCollectionIfMissing(dialogCollection, undefined, null);
        expect(expectedResult).toEqual(dialogCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
