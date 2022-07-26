export interface ISubjects {
  id?: number;
  subjectName?: string;
  numberSmestar?: number;
}

export class Subjects implements ISubjects {
  constructor(public id?: number, public subjectName?: string, public numberSmestar?: number) {}
}

export function getSubjectsIdentifier(subjects: ISubjects): number | undefined {
  return subjects.id;
}
