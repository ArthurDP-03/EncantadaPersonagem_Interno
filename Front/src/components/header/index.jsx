import './index.css'
import { useAuth } from '../../context/AuthContext'


function Header() {
    const { user } = useAuth()  
    console.log(user)
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
                            <div className="imagem-container">
                                <img src="" alt="" />
                            </div>
                            <div className="imagem-container">
                                <img src="" alt="" />
                            </div>
                        </div>
                    </div>
                </div>
            </section>
            <section className='navegacao'>
                <div className='lista-link'>
                    <a href="" className='link'>
                        <div className="texto"><p>Eventos</p></div>
                        <div className="icone">
                        </div>
                    </a>
                    <a href="" className='link'>
                        <div className="texto"><p>Eventos</p></div>
                        <div className="icone">
                        </div>
                    </a>
                    <a href="" className='link'>
                        <div className="texto"><p>Eventos</p></div>
                        <div className="icone">
                        </div>
                    </a>
                    <a href="" className='link'>
                        <div className="texto"><p>Eventos</p></div>
                        <div className="icone">
                        </div>
                    </a>
                    <a href="" className='link'>
                        <div className="texto"><p>Eventos</p></div>
                        <div className="icone">
                        </div>
                    </a>
                </div>
            </section>
        </header>
    )
}

export default Header;