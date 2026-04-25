import Button from '../components/Button.jsx'

function Dashboard({ onLogout }) {
  return (
    <main className="page-shell dashboard-shell">
      <section className="dashboard-card">
        <div className="dashboard-header">
          <div>
            <p className="eyebrow">Protected dashboard</p>
            <h1>Welcome back</h1>
          </div>

          <Button onClick={onLogout}>Logout</Button>
        </div>
      </section>
    </main>
  )
}

export default Dashboard
