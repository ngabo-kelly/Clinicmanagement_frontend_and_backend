import { useEffect, useState } from 'react'
import axios from 'axios'

const OFFICES_URL = 'http://localhost:8082/api/offices'

function OfficeList() {
  const [offices, setOffices] = useState([])
  const [search, setSearch] = useState('')
  const [message, setMessage] = useState('')
  const [isError, setIsError] = useState(false)

  async function loadOffices() {
    const response = await axios.get(OFFICES_URL + '/all')
    setOffices(response.data)
  }

  useEffect(() => {
    loadOffices().catch((error) => {
      setIsError(true)
      setMessage(error.message || 'Could not load offices.')
    })
  }, [])

  const query = search.trim().toLowerCase()
  const filtered = offices.filter((office) => {
    if (!query) {
      return true
    }
    const name = String(office.name || '').toLowerCase()
    const number = String(office.officeNumber || '')
    return name.includes(query) || number.includes(query)
  })

  async function handleDelete(office) {
    const ok = window.confirm(
      'Delete ' + office.name + ' (office ' + office.officeNumber + ')?'
    )
    if (!ok) {
      return
    }

    try {
      await axios.delete(OFFICES_URL + '/delete/' + office.id)
      setIsError(false)
      setMessage('Office deleted successfully!')
      await loadOffices()
    } catch (error) {
      setIsError(true)
      setMessage(
        error.response?.data || error.message || 'Failed to delete office.'
      )
    }
  }

  return (
    <div className="office-list-page">
      <h1>Available offices</h1>

      <div className="search-row">
        <label htmlFor="officeSearch">Search:</label>
        <input
          id="officeSearch"
          type="text"
          value={search}
          placeholder="Office name or office number"
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {message ? (
        <p className={isError ? 'save-office-error' : 'save-office-success'}>
          {String(message)}
        </p>
      ) : null}

      <table className="office-table">
        <thead>
          <tr>
            <th>Office name</th>
            <th>Office number</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {filtered.length === 0 ? (
            <tr>
              <td colSpan="3">No offices found.</td>
            </tr>
          ) : (
            filtered.map((office) => (
              <tr key={office.id}>
                <td>{office.name}</td>
                <td>{office.officeNumber}</td>
                <td className="action-cell">
                  <button type="button" onClick={() => handleDelete(office)}>
                    Delete
                  </button>
                  <button type="button">Update</button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  )
}

export default OfficeList
