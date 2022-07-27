export interface ISubjects {
  id?: number;
  subjectName?: string;
  numberSemestars?: number;
}

export class Subjects implements ISubjects {
  constructor(public id?: number, public subjectName?: string, public numberSemestars?: number) {}
}

export function getSubjectsIdentifier(subjects: ISubjects): number | undefined {
  return subjects.id;
}
