import "./index.css";
import { useDashboard } from "../../hooks/useDashboard";
import { formatarStatus } from "../../utils/formatters";
import { useTranslation } from "react-i18next";

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
  const { t, i18n } = useTranslation();
  const locale = i18n.resolvedLanguage || i18n.language || "pt-BR";

  if (loading) return <p>{t("common.loading")}</p>;
  if (error) return <p>{t("common.error", { message: error })}</p>;
  if (!data) return null;

  const statusChart = {
    labels: Object.keys(data.eventosPorStatus).map((status) =>
      formatarStatus(status),
    ),
    datasets: [
      {
        data: Object.values(data.eventosPorStatus),
        backgroundColor: ["#2d6a4f", "#40916c", "#74c69d", "#f59e0b"],
        borderWidth: 0,
      },
    ],
  };

  const eventosPorMes = [...data.eventosPorMes].sort((a, b) =>
    a.mes.localeCompare(b.mes),
  );

  const eventosChart = {
    labels: eventosPorMes.map((item) => item.mes),
    datasets: [
      {
        label: t("dashboard.eventsLabel"),
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
          <h1 className="titulo t1">{t("dashboard.title")}</h1>

          <div className="dashboard-cards">
            <div className="dashboard-card">
              <span>{t("dashboard.cards.totalEvents")}</span>
              <strong>{data.totalEventos}</strong>
            </div>

            <div className="dashboard-card">
              <span>{t("dashboard.cards.billing")}</span>
              <strong>
                {data.faturamentoTotal.toLocaleString(locale, {
                  style: "currency",
                  currency: "BRL",
                })}
              </strong>
            </div>

            <div className="dashboard-card">
              <span>{t("dashboard.cards.activeActors")}</span>
              <strong>{data.totalAtoresAtivos}</strong>
            </div>

            <div className="dashboard-card">
              <span>{t("dashboard.cards.pendingInvites")}</span>
              <strong>{data.convitesPendentes}</strong>
            </div>
          </div>

          <div className="dashboard-grid">
            <div className="dashboard-widget">
              <h2>{t("dashboard.charts.status")}</h2>

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
              <h2>{t("dashboard.charts.byMonth")}</h2>

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
            <h2>{t("dashboard.charts.upcoming")}</h2>

            <table className="dashboard-table">
              <thead>
                <tr>
                  <th>{t("dashboard.table.event")}</th>
                  <th>{t("dashboard.table.client")}</th>
                  <th>{t("dashboard.table.date")}</th>
                  <th>{t("dashboard.table.status")}</th>
                </tr>
              </thead>

              <tbody>
                {data.proximosEventos.map((evento) => (
                  <tr key={`${evento.titulo}-${evento.dataInicio}`}>
                    <td>{evento.titulo}</td>
                    <td>{evento.cliente}</td>
                    <td>{new Date(evento.dataInicio).toLocaleDateString(locale)}</td>
                    <td>{formatarStatus(evento.status)}</td>
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
