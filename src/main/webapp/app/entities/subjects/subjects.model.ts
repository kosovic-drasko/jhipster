import { IStudent } from 'app/entities/student/student.model';

export interface ISubjects {
  id?: number;
  subjectName?: string;
  numberSmestar?: number;
  student?: IStudent | null;
}

export class Subjects implements ISubjects {
  constructor(public id?: number, public subjectName?: string, public numberSmestar?: number, public student?: IStudent | null) {}
}

export function getSubjectsIdentifier(subjects: ISubjects): number | undefined {
  return subjects.id;
}
