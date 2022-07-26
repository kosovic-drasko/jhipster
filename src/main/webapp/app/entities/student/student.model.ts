import { ISubjects } from 'app/entities/subjects/subjects.model';

export interface IStudent {
  id?: number;
  name?: string;
  age?: number;
  subjects?: ISubjects[] | null;
}

export class Student implements IStudent {
  constructor(public id?: number, public name?: string, public age?: number, public subjects?: ISubjects[] | null) {}
}

export function getStudentIdentifier(student: IStudent): number | undefined {
  return student.id;
}
