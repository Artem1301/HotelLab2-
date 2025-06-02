import '@testing-library/jest-dom';
import { server } from './mocks/server';

// Увімкнути MSW перед усіма тестами
beforeAll(() => server.listen());
// Скинути обробники після кожного тесту
afterEach(() => server.resetHandlers());
// Вимкнути MSW після всіх тестів
afterAll(() => server.close());