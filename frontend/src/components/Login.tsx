import LoginForm from './LoginForm';

function Login() {
  const handleLogin = ({ email, password }: { email: string; password: string }) => {
    console.log('Login intentado con:', email, password);
    // Aquí luego conectarás con tu backend Spring
  };

  return (
    <div className="login-container">
      <LoginForm onLogin={handleLogin} />
    </div>
  );
}

export default Login;
