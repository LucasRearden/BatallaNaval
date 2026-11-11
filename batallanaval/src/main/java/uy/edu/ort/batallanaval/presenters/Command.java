package uy.edu.ort.batallanaval.presenters;




    public class Command {
        private String id;
        private Object parametro;

        public Command(String id, Object parametro) {
            this.id = id;
            this.parametro = parametro;
        }

        public Command() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Object getParametro() {
            return parametro;
        }

        public void setParametro(Object parametro) {
            this.parametro = parametro;
        }
    }


