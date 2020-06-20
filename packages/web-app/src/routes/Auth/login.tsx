import { FunctionalComponent, h } from 'preact';
import { useEffect } from 'preact/hooks';
import { useDispatch } from 'react-redux';

import scrumCards from 'assets/icons/scrumCards.png';
import { login, logout } from 'stores/authStore';
import { route } from 'preact-router';

const Login: FunctionalComponent = () => {
    const dispatch = useDispatch();

    useEffect(() => {
        dispatch(logout());
    });

    const linkTo = (): void => {
        dispatch(login());
        route('/', true);
    };

    return (
        <div class="login-page">
            <div class="form-container login-form">
                <h1 class="login-title">Scrumble</h1>
                <img class="h-20 w-20 mx-auto" src={scrumCards} alt="Image of Scrum Cards" />
                <button class="btn-create mx-auto my-auto" onClick={linkTo}>
                    Login with GitLab
                </button>
            </div>
        </div>
    );
};

export default Login;
