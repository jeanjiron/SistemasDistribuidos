/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tarea4;

/**
 *
 * @author juancarlos
 */
public class Tarea4 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        HechoDAO hd=new HechoDAO();
        Hecho echo=new Hecho();
        echo.setNombreHecho("hurra");
        echo.setIdHecho(9);
        hd.update(echo);
        
        hd.readAll().forEach(System.out::println);
    }
    
}
