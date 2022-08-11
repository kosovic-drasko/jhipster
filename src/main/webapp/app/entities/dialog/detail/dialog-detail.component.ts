import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IDialog } from '../dialog.model';

@Component({
  selector: 'jhi-dialog-detail',
  templateUrl: './dialog-detail.component.html',
})
export class DialogDetailComponent implements OnInit {
  dialog: IDialog | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ dialog }) => {
      this.dialog = dialog;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
