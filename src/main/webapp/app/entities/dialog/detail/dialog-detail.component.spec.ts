import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DialogDetailComponent } from './dialog-detail.component';

describe('Dialog Management Detail Component', () => {
  let comp: DialogDetailComponent;
  let fixture: ComponentFixture<DialogDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DialogDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ dialog: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(DialogDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(DialogDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load dialog on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.dialog).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
