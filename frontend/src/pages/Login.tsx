import type { FormEvent } from 'react';

const Login: React.FC = () => {
  const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    // Lógica para manejar el envío del formulario
  };

  return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="p-8 bg-white shadow-md rounded-md w-full max-w-sm">
        <h2 className="text-2xl font-bold mb-6 text-center">Iniciar sesión</h2>
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            placeholder="Usuario"
            className="w-full px-4 py-2 mb-4 border border-gray-300 rounded"
          />
          <input
            type="password"
            placeholder="Contraseña"
            className="w-full px-4 py-2 mb-4 border border-gray-300 rounded"
          />
          <button
            type="submit"
            className="w-full bg-blue-500 text-white py-2 rounded hover:bg-blue-600"
          >
            Ingresar
          </button>
        </form>
      </div>
    </div>
  );
};

export default Login;
