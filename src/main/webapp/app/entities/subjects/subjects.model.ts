export interface ISubjects {
  id: number;
  nameSubject?: string | null;
  grade?: number | null;
}

export type NewSubjects = Omit<ISubjects, 'id'> & { id: null };
