import './index.css'
import { NavLink, useLocation } from 'react-router-dom'
import useLogout from '../../hooks/useLogout'
import { useAuth } from '../../context/AuthContext'
import { House, CalendarDays, BarChart2, Handshake, Drama, Users, Bell, Moon, LogOut } from 'lucide-react'

function Header() {
    const { user } = useAuth()
    const { handleLogout } = useLogout()

    const links = [
        { label: 'Geral', href: '/', icone: <House size={18} /> },
        { label: 'Eventos', href: '/eventos', icone: <CalendarDays size={18} /> },
        { label: 'Financeiro', href: '/financeiro', icone: <BarChart2 size={18} /> },
        { label: 'Clientes', href: '/clientes', icone: <Handshake size={18} /> },
        { label: 'Personagens', href: '/personagens', icone: <Drama size={18} /> },
        { label: 'Colaboradores', href: '/colaboradores', icone: <Users size={18} /> },
    ]

    return (
        <header>
            <section className="cabecalho">
                <div className="conteudo-95">
                    <div className="conteudo">
                        <div className="perfil">
                            <div className="imagem-container">
                                <img src="" alt="" className="foto_perfil" />
                            </div>
                            <div className="textos">
                                <div className="nome">
                                    <p>{user?.name ?? 'UserName'}</p>
                                </div>
                                <div className="cargo">
                                    <p>{user?.role == 'ADMIN' ? 'Administrador' : 'Ator'}</p>
                                </div>
                            </div>
                        </div>
                        <div className="logo">
                            <div className="imagem-container">
                                <img src="src/assets/logo.png" alt="" />
                            </div>
                        </div>
                        <div className="funcionalidades">
                            <div className="icone">
                                <Moon size={30} />
                            </div>
                            <div className="icone">
                                <Bell size={30} />
                            </div>
                        </div>
                    </div>
                </div>
            </section>
            <section className='navegacao'>
                <div className='lista-link'>
                    {links.map(({ label, href, icone }) => (
                        <NavLink key={href} to={href} end={href === '/'} className={({ isActive }) => `link ${isActive ? 'ativo' : ''}`}>
                            <div className="texto"><p>{label}</p></div>
                            <div className="icone">{icone}</div>
                        </NavLink>
                    ))}
                </div>
                <div className='lista-link'>
                    <a onClick={handleLogout} className='link'>
                        <div className="texto"><p>Sair</p></div>
                        <div className="icone"><LogOut size={18} /></div>
                    </a>
                </div>

            </section>
        </header>
    )
}

export default Header;