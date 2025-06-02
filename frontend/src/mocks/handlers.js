import { rest } from 'msw';

export const handlers = [
    rest.post('http://localhost:8080/auth/register', (req, res, ctx) => {
        const { email, password } = req.body;
        if (!email || !password) {
            return res(
                ctx.status(400),
                ctx.json({ message: 'Помилка реєстрації: Email і пароль обов’язкові' })
            );
        }
        return res(
            ctx.status(200),
            ctx.json({ message: 'Користувач успішно зареєстрований' })
        );
    }),
    rest.post('http://localhost:8080/auth/login', (req, res, ctx) => {
        const { email, password } = req.body;
        if (email === 'test@example.com' && password === 'test123') {
            return res(
                ctx.status(200),
                ctx.json('mocked-jwt-token')
            );
        }
        return res(
            ctx.status(400),
            ctx.json({ message: 'Помилка входу: Невірний email або пароль' })
        );
    }),
];