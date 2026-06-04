import "./index.css";
import { useDashboard } from "../../hooks/useDashboard";

import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  ArcElement,
  Tooltip,
  Legend,
} from "chart.js";

import { Bar, Doughnut } from "react-chartjs-2";

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  ArcElement,
  Tooltip,
  Legend
);

function Dashboard() {
  const { data, loading, error } = useDashboard();

  if (loading) return <p>Carregando...</p>;
  if (error) return <p>Erro: {error}</p>;
  if (!data) return null;

  const statusChart = {
    labels: Object.keys(data.eventosPorStatus),
    datasets: [
      {
        data: Object.values(data.eventosPorStatus),
        backgroundColor: [
          "#2d6a4f",
          "#40916c",
          "#74c69d",
          "#f59e0b",
        ],
        borderWidth: 0,
      },
    ],
  };

  const eventosPorMes = [...data.eventosPorMes].sort((a, b) =>
    a.mes.localeCompare(b.mes)
  );

  const eventosChart = {
    labels: eventosPorMes.map((item) => item.mes),
    datasets: [
      {
        label: "Eventos",
        data: eventosPorMes.map((item) => item.quantidade),
        backgroundColor: "#2d6a4f",
        borderRadius: 8,
      },
    ],
  };

  return (
    <section className="section-dashboard">
      <div className="conteudo-95 layout">
        <div className="conteudo">
          <h1 className="titulo t1">Dashboard</h1>

          <div className="dashboard-cards">
            <div className="dashboard-card">
              <span>Total de Eventos</span>
              <strong>{data.totalEventos}</strong>
            </div>

            <div className="dashboard-card">
              <span>Faturamento</span>
              <strong>
                {data.faturamentoTotal.toLocaleString("pt-BR", {
                  style: "currency",
                  currency: "BRL",
                })}
              </strong>
            </div>

            <div className="dashboard-card">
              <span>Atores Ativos</span>
              <strong>{data.totalAtoresAtivos}</strong>
            </div>

            <div className="dashboard-card">
              <span>Convites Pendentes</span>
              <strong>{data.convitesPendentes}</strong>
            </div>
          </div>

          <div className="dashboard-grid">
            <div className="dashboard-widget">
              <h2>Status dos Eventos</h2>

              <div className="chart-container">
                <Doughnut
                  data={statusChart}
                  options={{
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                      legend: {
                        position: "bottom",
                      },
                    },
                  }}
                />
              </div>
            </div>

            <div className="dashboard-widget">
              <h2>Eventos por Mês</h2>

              <div className="chart-container">
                <Bar
                  data={eventosChart}
                  options={{
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                      legend: {
                        display: false,
                      },
                    },
                  }}
                />
              </div>
            </div>
          </div>

          <div className="dashboard-widget">
            <h2>Próximos Eventos</h2>

            <table className="dashboard-table">
              <thead>
                <tr>
                  <th>Evento</th>
                  <th>Cliente</th>
                  <th>Data</th>
                  <th>Status</th>
                </tr>
              </thead>

              <tbody>
                {data.proximosEventos.map((evento) => (
                  <tr
                    key={`${evento.titulo}-${evento.dataInicio}`}
                  >
                    <td>{evento.titulo}</td>
                    <td>{evento.cliente}</td>
                    <td>
                      {new Date(
                        evento.dataInicio
                      ).toLocaleDateString("pt-BR")}
                    </td>
                    <td>{evento.status}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </section>
  );
}

export default Dashboard;