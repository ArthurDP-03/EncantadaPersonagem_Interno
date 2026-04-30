import { useEventos } from '../../hooks/useEventos';
import './index.css'
import { Search, ChevronDown } from "lucide-react";
import { useState } from "react";
import logo from '../../assets/logo.png'
import { formatarPeriodoEvento, agruparEventosPorData, labelData, formatarDataCurta } from '../../utils/formatters';

function Eventos() {
  const { eventos, carregando, erro } = useEventos();
  const [imagem] = useState(logo);

  if (carregando) return <p>Carregando...</p>;
  if (erro) return <p>Erro: {erro}</p>;

  const grupos = agruparEventosPorData(eventos);

  return (
    <section className="section-eventos">
      <div className="conteudo-95 layout">
        <div className="conteudo">
          <h1 className="titulo t1">Eventos</h1>
          <div className="filtros">
            <div className="input-container">
              <select className="input">
                <option value="">Ordenar</option>
                <option value="">Alfabética(A-Z)</option>
                <option value="">Alfabética(Z-A)</option>
                <option value="">Disponibilidade</option>
              </select>
              <ChevronDown className="icon" size={18} />
            </div>
            <div className="input-container">
              <input type="text" placeholder="Buscar" className="input" />
              <Search className="icon" size={18} />
            </div>
          </div>

          <div className="timeline">
            {grupos.map((item, idx) => {
              if (item.tipo === 'vazio') {
                return (
                  <div key={idx} className="grupo-vazio">
                    <p className="label-sem-eventos">
                      Nada planejado entre {formatarDataCurta(item.inicio)} e {formatarDataCurta(item.fim)}
                    </p>
                  </div>
                );
              }

              const { prefixo, texto } = labelData(item.data);

              return (
                <div key={idx} className="grupo-data">
                  <div className="label-data">
                    {prefixo && (
                      <span className={`badge badge-${prefixo.toLowerCase()}`}>
                        {prefixo}
                      </span>
                    )}
                    <span className="texto-data">{texto}</span>
                  </div>

                  <ul className="lista-eventos">
                    {item.eventos.map(evento => (
                      <li key={evento.id} className="card-eventos">
                        <div className="imagem-container">
                          <img src={imagem} alt="" className="imagem" />
                        </div>
                        <div className="textos">
                          <div className="texto t1"><p>{evento.titulo}</p></div>
                          <div className="texto t2"><p>{formatarPeriodoEvento(evento.dataInicio, evento.dataFim)}</p></div>
                          <div className="texto t2"><p>{evento.endereco}</p></div>
                        </div>
                      </li>
                    ))}
                  </ul>
                </div>
              );
            })}
          </div>

        </div>
      </div>
    </section>
  );
}

export default Eventos;