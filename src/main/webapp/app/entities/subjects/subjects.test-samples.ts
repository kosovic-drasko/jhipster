import { ISubjects, NewSubjects } from './subjects.model';

export const sampleWithRequiredData: ISubjects = {
  id: 76398,
  nameSubject: 'Avon synthesize Fresh',
};

export const sampleWithPartialData: ISubjects = {
  id: 99658,
  nameSubject: 'GB',
};

export const sampleWithFullData: ISubjects = {
  id: 86681,
  nameSubject: 'syndicate payment',
  grade: 16558,
};

export const sampleWithNewData: NewSubjects = {
  nameSubject: 'Dollar Movies Concrete',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
