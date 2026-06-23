import "../eventos/index.css";
import { ChevronDown, Eye, Search } from "lucide-react";
import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import logo from "../../assets/logo.png";
import { getMinhasEscalacoes } from "../../services/escalacaoService";
import { getMeusEventosAtor } from "../../services/eventosService";
import { agruparEventosPorData, formatarPeriodoEvento, formatarStatus, labelData } from "../../utils/formatters";

const obterMensagemErro = (err, fallback) => err.data?.message || err.data?.error || fallback;

function EventosAtor() {
  const navigate = useNavigate();
  const [eventos, setEventos] = useState([]);
  const [escalacoes, setEscalacoes] = useState([]);
  const [busca, setBusca] = useState("");
  const [ordem, setOrdem] = useState("");
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    async function carregar() {
      try {
        setCarregando(true);
        setErro(null);
        const [eventosAtor, escalacoesAtor] = await Promise.all([
          getMeusEventosAtor(),
          getMinhasEscalacoes(),
        ]);

        setEventos(eventosAtor);
        setEscalacoes(escalacoesAtor);
      } catch (err) {
        console.error("Erro ao carregar eventos do ator:", err);
        const mensagem = obterMensagemErro(err, "Erro ao carregar seus eventos");
        setErro(mensagem);
        Swal.fire({
          icon: "error",
          title: "Não foi possível carregar seus eventos",
          text: mensagem,
        });
      } finally {
        setCarregando(false);
      }
    }

    carregar();
  }, []);

  const escalacoesPorEvento = useMemo(() => {
    return escalacoes.reduce((acc, escala) => {
      acc[escala.eventoId] = acc[escala.eventoId] || [];
      acc[escala.eventoId].push(escala);
      return acc;
    }, {});
  }, [escalacoes]);

  const eventosFiltrados = eventos
    .filter((evento) =>
      [evento.titulo, evento.endereco]
        .filter(Boolean)
        .some((valor) => valor.toLowerCase().includes(busca.toLowerCase()))
    )
    .sort((a, b) => {
      if (ordem === "az") return a.titulo.localeCompare(b.titulo);
      if (ordem === "za") return b.titulo.localeCompare(a.titulo);
      return 0;
    });

  const grupos = agruparEventosPorData(eventosFiltrados);

  if (carregando) return <p>Carregando seus eventos...</p>;
  if (erro) return <p>{erro}</p>;

  return (
    <section className="section-eventos">
      <div className="conteudo-95 layout">
        <div className="conteudo">
          <h1 className="titulo t1">Eventos</h1>

          <div className="filtros">
            <div className="input-container">
              <select className="input" value={ordem} onChange={(event) => setOrdem(event.target.value)}>
                <option value="">Ordenar</option>
                <option value="az">A-Z</option>
                <option value="za">Z-A</option>
              </select>
              <ChevronDown className="icon" size={18} />
            </div>
            <div className="input-container">
              <input
                type="text"
                placeholder="Buscar"
                className="input"
                value={busca}
                onChange={(event) => setBusca(event.target.value)}
              />
              <Search className="icon" size={18} />
            </div>
          </div>

          <div className="timeline">
            {grupos.length === 0 ? (
              <div className="eventos-vazio">Você ainda não está escalado em nenhum evento.</div>
            ) : (
              grupos.map((item, idx) => {
                const { prefixo, texto } = labelData(item.data);
                return (
                  <div key={idx} className="grupo-data">
                    <div className="label-data">
                      {prefixo && <span className="texto t1">{prefixo},&nbsp;</span>}
                      <span className="texto-data texto t1">{texto}</span>
                    </div>

                    <ul className="lista-eventos">
                      {item.eventos.map((evento) => {
                        const escalacoesEvento = escalacoesPorEvento[evento.id] || [];

                        return (
                          <li key={evento.id} className="card-eventos">
                            <div className="imagem-container">
                              <img src={logo} alt="" className="imagem" />
                              <div className="card-eventos-acoes">
                                <button
                                  className="btn-icone btn-editar"
                                  type="button"
                                  title="Ver evento"
                                  onClick={() => navigate(`/eventos/${evento.id}`)}
                                >
                                  <Eye size={16} />
                                </button>
                              </div>
                            </div>
                            <div className="textos">
                              <div className="texto t1"><p>{evento.titulo}</p></div>
                              <div className="texto t2"><p>{formatarStatus(evento.status)}</p></div>
                              <div className="texto t2"><p>{formatarPeriodoEvento(evento.dataInicio, evento.dataFim)}</p></div>
                              <div className="texto t2"><p>{evento.endereco}</p></div>
                              {escalacoesEvento.map((escala) => (
                                <div key={escala.id} className="texto t2">
                                  <p>{escala.personagemNome} · {escala.status.replaceAll("_", " ")}</p>
                                </div>
                              ))}
                            </div>
                          </li>
                        );
                      })}
                    </ul>
                  </div>
                );
              })
            )}
          </div>
        </div>
      </div>
    </section>
  );
}

export default EventosAtor;
