import { FunctionalComponent, h } from 'preact';
import { useContext, useEffect, useState } from 'preact/hooks';
import { route } from 'preact-router';
import { observer as mobxObserver } from 'mobx-react-lite';

// Re-typing functions with generics by assigning them to variables
// doesn't seem to work, so we create a new function :/
function observer<P>(props: P): any {
    return mobxObserver(props as any);
}

import { LogIn } from 'react-feather';

import { RegistrationUser } from 'models/User';
import { AuthStoreContext } from 'stores';

const Register: FunctionalComponent = observer(() => {
    const authStore = useContext(AuthStoreContext);

    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [accessToken, setAccessToken] = useState('');

    const [errors, setErrors] = useState('');
    const [inProgress, setInProgress] = useState(false);

    useEffect(() => authStore.logout, []);

    const submitDetails = (): void => {
        setInProgress(true);
        const credentials: RegistrationUser = { user: { firstName, lastName, email, password } };

        // TODO: No type hints here? login() was perfectly happy to take registration credentials?
        authStore.register(credentials).then((errors) => {
            setInProgress(false);
            if (errors) setErrors(errors);
            else if (authStore.isAuthenticated) route('/');
        });
    };

    return (
        <form>
            <div class="field">
                <div class="control">
                    <input
                        class="input is-large"
                        type="text"
                        placeholder="First Name"
                        value={firstName}
                        onInput={(e): void => setFirstName((e.target as HTMLInputElement).value)}
                    />
                </div>
            </div>
            <div class="field">
                <div class="control">
                    <input
                        class="input is-large"
                        type="text"
                        placeholder="Last Name"
                        value={lastName}
                        onInput={(e): void => setLastName((e.target as HTMLInputElement).value)}
                    />
                </div>
            </div>
            <div class="field">
                <div class="control">
                    <input
                        class="input is-large"
                        type="email"
                        placeholder="Email"
                        value={email}
                        onInput={(e): void => {
                            setErrors('');
                            setEmail((e.target as HTMLInputElement).value);
                        }}
                    />
                </div>
            </div>
            <div class="field">
                <div class="control">
                    <input
                        class="input is-large"
                        type="password"
                        placeholder="Password"
                        value={password}
                        onInput={(e): void => {
                            setErrors('');
                            setPassword((e.target as HTMLInputElement).value);
                        }}
                    />
                </div>
            </div>
            <div class="field">
                <div class="control">
                    <input
                        class="input is-large"
                        type="text"
                        placeholder="Personal Access Token"
                        value={accessToken}
                        onInput={(e): void => {
                            setAccessToken((e.target as HTMLInputElement).value);
                        }}
                    />
                </div>
            </div>
            <div class="field">
                <div class="control">
                    <h2 class="error is-size-5">{errors}</h2>
                </div>
            </div>
            <button
                class={
                    'button is-block is-deep-space-sparkle is-large is-fullwidth' + (inProgress ? ' is-loading' : '')
                }
                type="button"
                onClick={(): void => submitDetails()}
            >
                <div class="level">
                    <div class="level-item">
                        <span>Submit</span>
                        <span class="icon is-small">{/*<LogIn />*/}</span>
                    </div>
                </div>
            </button>
        </form>
    );
});

export default Register;