import { useState, useEffect } from "react";
import { getEventos } from "../services/eventosService";

export function useEventos() {
  const [eventos, setEventos] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    getEventos()
        .then(setEventos)
        .catch(err => {
            console.error("Erro completo:", err);
            setErro(err.message);
        })
        .finally(() => setCarregando(false));
  }, []);

  return { eventos, carregando, erro };
}