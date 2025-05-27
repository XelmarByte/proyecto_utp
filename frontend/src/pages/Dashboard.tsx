import Example from '../components/layout/TablaUsers'

const Dashboard: React.FC = () => {
  return (
    <div className="min-h-screen bg-gray-100 flex">
      {/* Sidebar */}
      <aside className="w-64 bg-white shadow-md p-4">
        <h2 className="text-xl font-bold mb-6">Mi Dashboard</h2>
        <nav>
          <ul className="space-y-2">
            <li className="hover:text-blue-600 cursor-pointer">Inicio</li>
            <li className="hover:text-blue-600 cursor-pointer">Usuarios</li>
            <li className="hover:text-blue-600 cursor-pointer">Reportes</li>
            <li className="hover:text-blue-600 cursor-pointer">Ajustes</li>
          </ul>
        </nav>
      </aside>

      {/* Contenido principal */}
      <main className="flex-1 p-6">
        <h1 className="text-3xl font-semibold mb-4">DASHBOARD</h1>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div className="bg-white p-4 shadow rounded-md">
            <h3 className="text-lg font-medium">Estadísticas</h3>
            <p className="text-gray-500">Datos generales aquí</p>
          </div>
          <div className="bg-white p-4 shadow rounded-md">
            <h3 className="text-lg font-medium">Actividades recientes</h3>
            <p className="text-gray-500">Últimos movimientos del sistema</p>
          </div>
          <div className="bg-white p-4 shadow rounded-md">
            <h3 className="text-lg font-medium">Usuarios activos</h3>
            <p className="text-gray-500">Resumen del día</p>
          </div>
        </div>
        <div className='mt-6'>
          <Example/>
        </div>
      </main>
      
    </div>
  );
};

export default Dashboard;
