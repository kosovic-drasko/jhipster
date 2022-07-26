import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SubjectsService } from '../service/subjects.service';
import { ISubjects, Subjects } from '../subjects.model';

import { SubjectsUpdateComponent } from './subjects-update.component';

describe('Subjects Management Update Component', () => {
  let comp: SubjectsUpdateComponent;
  let fixture: ComponentFixture<SubjectsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let subjectsService: SubjectsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SubjectsUpdateComponent],
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
      .overrideTemplate(SubjectsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SubjectsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    subjectsService = TestBed.inject(SubjectsService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const subjects: ISubjects = { id: 456 };

      activatedRoute.data = of({ subjects });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(subjects));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Subjects>>();
      const subjects = { id: 123 };
      jest.spyOn(subjectsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ subjects });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: subjects }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(subjectsService.update).toHaveBeenCalledWith(subjects);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Subjects>>();
      const subjects = new Subjects();
      jest.spyOn(subjectsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ subjects });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: subjects }));
      saveSubject.complete();

      // THEN
      expect(subjectsService.create).toHaveBeenCalledWith(subjects);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Subjects>>();
      const subjects = { id: 123 };
      jest.spyOn(subjectsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ subjects });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(subjectsService.update).toHaveBeenCalledWith(subjects);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
