
package eliabJarabaServer.imc.modelo;

import java.io.Serializable;




public class calculoimc  implements Serializable{

    private float peso;
    private float altura;
    
   public static class Imc {
        
        public float resultado;
        public String mensaje;

        public float getResultado() {
            return resultado;
        }

        public void setResultado(float resultado) {
            this.resultado = resultado;
        }

        public String getMensaje() {
            return mensaje;
        }

        public void setMensaje(String mensaje) {
            this.mensaje = mensaje;
        }
        
    }
    
   public Imc imc;
    
    public calculoimc (){}
    
    public calculoimc(float peso, float altura){
        this.peso =peso;
        this.altura = altura;
    }
    
    public Imc getImc(){
        imc =new Imc();
        if(peso <= 0 || altura <=0){
            imc.mensaje = "ERROR: El peso y la altura deben ser mayores que 0";
            return imc;
        }else{
            imc.resultado = peso / (altura * altura);
            if(imc.resultado < 18.5){
                imc.mensaje = " Debes consultar de un medico, tu peso es muy bajo ";
            }else if(imc.resultado >= 18.5 && imc.resultado <= 24.9){
                imc.mensaje = "  Estan bien de peso  ";
            }else if(imc.resultado >= 24.9 && imc.resultado <= 29.9){
                imc.mensaje = "  Debes bajar un poco de peso  ";
            }else {
                imc.mensaje = "  Debes consultar de un medico, tu peso es muy alto ";
            }
            return imc;
        }
    }
    
    public static void main(String[] args) {
        calculoimc e = new calculoimc(170, 70);
        e.getImc();
        
        System.out.println("resultado: " + e.imc.getResultado());
        System.out.println("mensaje: " + e.imc.getMensaje());       
                
    }
    
    public float getPeso(){
        return peso;
    }
    
     public void setPeso(float peso){
         this.peso=peso;
    }
    
     public float getAltura(){
        return altura;
    }
    
     public void setAltura(float altura){
         this.altura=altura;
    }
}
