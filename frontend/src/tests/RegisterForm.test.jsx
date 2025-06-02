import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import RegisterForm from './RegisterForm';
import { server } from './mocks/server';
import { rest } from 'msw';

describe('RegisterForm', () => {
    test('успішна реєстрація відображає повідомлення про успіх', async () => {
        render(<RegisterForm />);

        fireEvent.change(screen.getByTestId('email-input'), {
            target: { value: 'test@example.com' },
        });
        fireEvent.change(screen.getByTestId('password-input'), {
            target: { value: 'test123' },
        });
        fireEvent.click(screen.getByTestId('submit-button'));

        await waitFor(() => {
            expect(screen.getByTestId('message')).toHaveTextContent('Користувач успішно зареєстрований');
        });
    });

    test('порожній email викликає помилку', async () => {
        render(<RegisterForm />);

        fireEvent.change(screen.getByTestId('email-input'), {
            target: { value: '' },
        });
        fireEvent.change(screen.getByTestId('password-input'), {
            target: { value: 'test123' },
        });
        fireEvent.click(screen.getByTestId('submit-button'));

        await waitFor(() => {
            expect(screen.getByTestId('message')).toHaveTextContent(
                'Помилка реєстрації: Email і пароль обов’язкові'
            );
        });
    });

    test('помилка мережі відображає загальне повідомлення про помилку', async () => {
        server.use(
            rest.post('http://localhost:8080/auth/register', (req, res, ctx) => {
                return res.networkError('Мережева помилка');
            })
        );

        render(<RegisterForm />);

        fireEvent.change(screen.getByTestId('email-input'), {
            target: { value: 'test@example.com' },
        });
        fireEvent.change(screen.getByTestId('password-input'), {
            target: { value: 'test123' },
        });
        fireEvent.click(screen.getByTestId('submit-button'));

        await waitFor(() => {
            expect(screen.getByTestId('message')).toHaveTextContent('Помилка реєстрації');
        });
    });

    test('некоректний формат email викликає помилку', async () => {
        server.use(
            rest.post('http://localhost:8080/auth/register', (req, res, ctx) => {
                const { email } = req.body;
                if (!email.includes('@')) {
                    return res(
                        ctx.status(400),
                        ctx.json({ message: 'Помилка реєстрації: Некоректний формат email' })
                    );
                }
                return res(ctx.status(200), ctx.json({ message: 'Користувач успішно зареєстрований' }));
            })
        );

        render(<RegisterForm />);

        fireEvent.change(screen.getByTestId('email-input'), {
            target: { value: 'invalid-email' },
        });
        fireEvent.change(screen.getByTestId('password-input'), {
            target: { value: 'test123' },
        });
        fireEvent.click(screen.getByTestId('submit-button'));

        await waitFor(() => {
            expect(screen.getByTestId('message')).toHaveTextContent(
                'Помилка реєстрації: Некоректний формат email'
            );
        });
    });
});