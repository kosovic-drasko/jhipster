import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { DialogService } from '../service/dialog.service';

import { DialogComponent } from './dialog.component';

describe('Dialog Management Component', () => {
  let comp: DialogComponent;
  let fixture: ComponentFixture<DialogComponent>;
  let service: DialogService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [DialogComponent],
    })
      .overrideTemplate(DialogComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(DialogService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.dialogs?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
