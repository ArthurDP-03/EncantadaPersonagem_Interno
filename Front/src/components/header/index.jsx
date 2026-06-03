import "./index.css";
import { Navigate, NavLink } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import {
  House,
  CalendarDays,
  BarChart2,
  Handshake,
  Drama,
  Users,
  Bell,
  Moon,
  Sun,
  LogOut,
} from "lucide-react";
import { useDarkMode } from "../../hooks/useDarkMode";

function Header() {
  const { user, logout } = useAuth();
  const [ dark, setDark ] = useDarkMode();

  function handleLogout() {
    logout();
    Navigate("/login");
  }

  // define links por perfil
  const links =
    user?.role === "ADMIN"
      ? //se for admin
        [
          { label: "Geral", href: "/", icone: <House size={18} /> }, // revisar como vai ser essa navegação sem sair da tela de admin.
          {
            label: "Eventos",
            href: "/eventos",
            icone: <CalendarDays size={18} />,
          },
          {
            label: "Financeiro",
            href: "/financeiro",
            icone: <BarChart2 size={18} />,
          },
          {
            label: "Clientes",
            href: "/clientes",
            icone: <Handshake size={18} />,
          },
          {
            label: "Personagens",
            href: "/personagens",
            icone: <Drama size={18} />,
          },
          {
            label: "Colaboradores",
            href: "/colaboradores",
            icone: <Users size={18} />,
          },
        ]
      : //se nao for admin, no caso ator
        [
          { label: "Geral", href: "/", icone: <House size={18} /> }, //editar permissoes depois do que ator pode acessar
          {
            label: "Eventos",
            href: "/eventos",
            icone: <CalendarDays size={18} />,
          }, //editar permissoes depois do que ator pode acessar
        ];

  if (!user) return null;

  return (
    <header className="cabecalho">
      <section className="cabecalho-section">
        <div className="conteudo-95">
          <div className="conteudo">
            <div className="perfil">
              <div className="imagem-container">
                <img src="" alt="" className="foto_perfil" />
              </div>

              <div className="textos">
                <div className="nome">
                  <p>{user.name}</p>
                </div>
                <div className="cargo">
                  <p>{user.role === "ADMIN" ? "Administrador" : "Ator"}</p>
                </div>
              </div>
            </div>

            <div className="logo">
              <div className="imagem-container">
                <img src="src/assets/logo.png" alt="" />
              </div>
            </div>

            <div className="funcionalidades">
              <div className="icone" onClick={() => setDark((d) => !d)}>
                {dark ? <Sun size={30} /> : <Moon size={30} />}
              </div>
              <div className="icone">
                <Bell size={30} />
              </div>
            </div>
          </div>
        </div>
      </section>

      <section className="navegacao">
        <div className="lista-link">
          {links.map(({ label, href, icone }) => (
            <NavLink
              key={href}
              to={href}
              className={({ isActive }) => `link ${isActive ? "ativo" : ""}`}
            >
              <div className="texto">
                <p>{label}</p>
              </div>
              <div className="icone">{icone}</div>
            </NavLink>
          ))}
        </div>

        <div className="lista-link">
          <button onClick={handleLogout} className="link logout">
            <div className="texto">
              <p>Sair</p>
            </div>
            <div className="icone">
              <LogOut size={18} />
            </div>
          </button>
        </div>
      </section>
    </header>
  );
}

export default Header;
