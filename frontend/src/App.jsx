import { useState } from 'react'
import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'

function App() {
  const [activePage, setActivePage] = useState('login')

  return (
    <div className="app-shell">
      {activePage === 'login' ? <Login onSwitch={setActivePage} /> : null}
      {activePage === 'register' ? <Register onSwitch={setActivePage} /> : null}
    </div>
  )
}

export default App
