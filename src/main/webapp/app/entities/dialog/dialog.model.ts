export interface IDialog {
  id?: number;
  name?: string | null;
}

export class Dialog implements IDialog {
  constructor(public id?: number, public name?: string | null) {}
}

export function getDialogIdentifier(dialog: IDialog): number | undefined {
  return dialog.id;
}
