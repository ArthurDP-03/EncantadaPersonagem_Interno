import Header from '../header'
import { Outlet } from 'react-router-dom'

function PrivateLayout() {
  return (
    <>
      <Header />
      <Outlet />
    </>
  )
}

export default PrivateLayout