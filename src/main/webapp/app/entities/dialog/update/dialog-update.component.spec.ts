import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DialogService } from '../service/dialog.service';
import { IDialog, Dialog } from '../dialog.model';

import { DialogUpdateComponent } from './dialog-update.component';

describe('Dialog Management Update Component', () => {
  let comp: DialogUpdateComponent;
  let fixture: ComponentFixture<DialogUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let dialogService: DialogService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [DialogUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(DialogUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DialogUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    dialogService = TestBed.inject(DialogService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const dialog: IDialog = { id: 456 };

      activatedRoute.data = of({ dialog });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(dialog));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Dialog>>();
      const dialog = { id: 123 };
      jest.spyOn(dialogService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dialog });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dialog }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(dialogService.update).toHaveBeenCalledWith(dialog);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Dialog>>();
      const dialog = new Dialog();
      jest.spyOn(dialogService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dialog });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dialog }));
      saveSubject.complete();

      // THEN
      expect(dialogService.create).toHaveBeenCalledWith(dialog);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Dialog>>();
      const dialog = { id: 123 };
      jest.spyOn(dialogService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dialog });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(dialogService.update).toHaveBeenCalledWith(dialog);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
