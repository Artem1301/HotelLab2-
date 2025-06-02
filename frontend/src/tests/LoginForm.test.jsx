import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import LoginForm from './LoginForm';
import { server } from './mocks/server';
import { rest } from 'msw';

describe('LoginForm', () => {
    test('успішний вхід відображає повідомлення з токеном', async () => {
        render(<LoginForm />);

        fireEvent.change(screen.getByTestId('email-input'), {
            target: { value: 'test@example.com' },
        });
        fireEvent.change(screen.getByTestId('password-input'), {
            target: { value: 'test123' },
        });
        fireEvent.click(screen.getByTestId('submit-button'));

        await waitFor(() => {
            expect(screen.getByTestId('message')).toHaveTextContent('Успішний вхід, токен: mocked-jwt-token');
        });
    });

    test('невірний пароль викликає помилку', async () => {
        render(<LoginForm />);

        fireEvent.change(screen.getByTestId('email-input'), {
            target: { value: 'test@example.com' },
        });
        fireEvent.change(screen.getByTestId('password-input'), {
            target: { value: 'wrongpassword' },
        });
        fireEvent.click(screen.getByTestId('submit-button'));

        await waitFor(() => {
            expect(screen.getByTestId('message')).toHaveTextContent('Помилка входу: Невірний email або пароль');
        });
    });

    test('порожній email викликає помилку', async () => {
        render(<LoginForm />);

        fireEvent.change(screen.getByTestId('email-input'), {
            target: { value: '' },
        });
        fireEvent.change(screen.getByTestId('password-input'), {
            target: { value: 'test123' },
        });
        fireEvent.click(screen.getByTestId('submit-button'));

        await waitFor(() => {
            expect(screen.getByTestId('message')).toHaveTextContent('Помилка входу: Невірний email або пароль');
        });
    });

    test('помилка мережі відображає загальне повідомлення про помилку', async () => {
        server.use(
            rest.post('http://localhost:8080/auth/login', (req, res, ctx) => {
                return res.networkError('Мережева помилка');
            })
        );

        render(<LoginForm />);

        fireEvent.change(screen.getByTestId('email-input'), {
            target: { value: 'test@example.com' },
        });
        fireEvent.change(screen.getByTestId('password-input'), {
            target: { value: 'test123' },
        });
        fireEvent.click(screen.getByTestId('submit-button'));

        await waitFor(() => {
            expect(screen.getByTestId('message')).toHaveTextContent('Помилка входу');
        });
    });
});