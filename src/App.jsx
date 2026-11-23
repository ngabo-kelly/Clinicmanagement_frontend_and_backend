import { useState } from 'react'
import './App.css'
import SaveOffice from './components/SaveOffice'
import OfficeList from './components/OfficeList'

function App() {
  const [page, setPage] = useState('offices')

  return (
    <div>
      <nav className="app-nav">
        <button type="button" onClick={() => setPage('offices')}>
          Available offices
        </button>
        <button type="button" onClick={() => setPage('save')}>
          Save office
        </button>
      </nav>
      {page === 'offices' ? <OfficeList /> : <SaveOffice />}
    </div>
  )
}

export default App
